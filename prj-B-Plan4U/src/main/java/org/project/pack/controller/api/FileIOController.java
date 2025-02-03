package org.project.pack.controller.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.project.pack.annotations.AuthUser;
import org.project.pack.classes.Randomizer;
import org.project.pack.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegBuilder.Strict;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.resizers.configurations.AlphaInterpolation;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Dithering;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;;

// /api/static/upload -> name=files
// 

@RestController
@RequestMapping("/api/static")
public class FileIOController {
	
	@Value("${upload.file.original}")
	String originalPath;
	@Value("${upload.file.thumbnail}")
	String thumbnailPath;
	@Value("${upload.file.clip}")
	String clipPath;
	@Value("${upload.file.image}")
	String imageExts;
	@Value("${upload.file.video}")
	String videoExts;
	@Value("${upload.file.name.length}")
	Integer randomNameLength;
	@Value("${upload.file.image.thumb.width}")
	Integer thumbWidth;
	@Value("${upload.file.image.thumb.format}")
	String thumbFormat;
	@Value("${upload.file.video.clip.format}")
	String clipFormat;
	@Value("${upload.file.ffmpeg.path}")
	String ffmpegPath;
	@Value("${upload.file.ffprobe.path}")
	String ffprobePath;
	@Value("${upload.file.ffmpeg.framerate}")
	Integer videoFramerate;
	@Value("${upload.file.ffmpeg.start}")
	Double videoStart;
	@Value("${upload.file.ffmpeg.duration}")
	Double videoDuration;
	@Value("${upload.file.ffmpeg.width}")
	Integer videoWidth;
	
	Path pt = Paths.get("src/main/resources/static/orginal");
	
	
	public Path ExistsFolder(Path path) throws IOException {
		Path folder = path.toAbsolutePath().getParent();
		if(!Files.exists(path)) Files.createDirectories(folder);
		return path;
	}
	public void Save(InputStream in, String path) throws IOException { Save(in, Paths.get(path)); }
	public void Save(InputStream in, Path path) throws IOException {
		Files.copy(in, ExistsFolder(path));
	}
	public String getExt(String name) {
		String[] spliter = name.split("\\.");
		if(spliter.length < 2) return "unknown";
		return spliter[spliter.length - 1];
	}
	public boolean inExts(String ext, String[] exts) {
		for(String ext_temp : exts) {
			if(ext.equalsIgnoreCase(ext_temp)) return true;
		}
		return false;
	}
	public double getVideoDuration(FFmpeg mpeg, FFprobe probe, String video) throws IOException {
		return probe.probe(video).getFormat().duration;
	}
	
	@PostMapping("/upload")
	public String upload(
		@RequestPart(value = "files") List<MultipartFile> files,
		@AuthUser User user,
		HttpServletRequest req
	) {
		Date now = new Date();
		String original = pt.toString();
		String thumb = req.getServletContext().getRealPath(thumbnailPath + "/" + now.getYear() + "/" + (now.getMonth() + 1) + "/" + now.getDate());
		String clip = req.getServletContext().getRealPath(clipPath + "/" + now.getYear() + "/" + (now.getMonth() + 1) + "/" + now.getDate());
		String[] imageExts = this.imageExts.split(",");
		String[] videoExts = this.videoExts.split(",");
		List<String> origins = new ArrayList<String>();
		List<String> thumbs = new ArrayList<String>();
		List<String> clips = new ArrayList<String>();
		for(MultipartFile file : files) {
			String ext = getExt(file.getOriginalFilename());
			String randomName = Randomizer.generateString(randomNameLength);
			try {
				String originFullpath = original + "/" + randomName + "." + ext;
				Save(file.getInputStream(), originFullpath);
				origins.add(originFullpath);
				if(inExts(ext, imageExts)) {
					String thumbFullpath = thumb + "/" + randomName + "." + thumbFormat;
					Builder<File> builder = Thumbnails
						.of(Paths.get(originFullpath).toFile())
						.alphaInterpolation(AlphaInterpolation.SPEED)
						.antialiasing(Antialiasing.ON)
						.dithering(Dithering.DISABLE)
						.width(thumbWidth)
						.scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
						.outputFormat(thumbFormat);
					if(thumbFormat.equalsIgnoreCase("jpg") || thumbFormat.equalsIgnoreCase("jpeg"))
						builder = builder.outputQuality(0.85);
					builder.toFile(ExistsFolder(Paths.get(thumbFullpath)).toFile());
					thumbs.add(thumbFullpath);
				} else if(inExts(ext, videoExts)) {
					String clipFullpath = clip + "/" + randomName + "." + clipFormat;
					new Thread(()->{
						try {
							FFmpeg mpeg = new FFmpeg(ffmpegPath);
							FFprobe probe = new FFprobe(ffprobePath);
							double duration = getVideoDuration(mpeg, probe, originFullpath);
							FFmpegBuilder builder = new FFmpegBuilder()
									.setInput(originFullpath)
									.addOutput(clipFullpath)
									.disableSubtitle()
									.disableAudio()
									.setStartOffset((int)(duration * videoStart), TimeUnit.SECONDS)
									.setDuration((int)(duration * videoDuration), TimeUnit.SECONDS)
									.setVideoFrameRate(videoFramerate)
									.setVideoWidth(videoWidth)
									.setStrict(Strict.EXPERIMENTAL)
									.done();
							new FFmpegExecutor(mpeg, probe).createJob(builder).run();
						} catch(Exception e) {}
					}).start();
					clips.add(clipFullpath);
				} else {}
			} catch(Exception e) {}
		}
		FileUploadAction(user, req, origins, thumbs, clips);
		return "{\"message\":\"success\"}";
	}
	
	@GetMapping("/{year}/{month}/{date}/{filename}")
	public ResponseEntity<byte[]> getMedia(
		@PathVariable("year") String year,
		@PathVariable("month") String month,
		@PathVariable("date") String date,
		@PathVariable("filename") String filename,
		@RequestParam(name = "direct", required = false) boolean direct,
		@RequestParam(name = "thumb", required = false) boolean thumb,
		HttpServletRequest req
	){
		String ext = getExt(filename);
		String name = filename.substring(0, filename.length() - ext.length() - 1);
		String path = year + "/" + month + "/" + date + "/" + name;
		String[] imageExts = this.imageExts.split(",");
		String[] videoExts = this.videoExts.split(",");
		if(thumb) {
			if(inExts(ext, imageExts)) path = thumbnailPath + "/" + path + "." + thumbFormat;
			else if(inExts(ext, videoExts)) path = clipPath + "/" + path + "." + clipFormat;
			else path = originalPath + "/" + path + "." + ext;
		}
		else path = originalPath + "/" + path + "." + ext;
		path = req.getServletContext().getRealPath(path).toString();
		try {
			ResponseEntity.BodyBuilder builder = new ResponseEntity<byte[]>(HttpStatus.OK).ok()
				.contentType(MediaType.valueOf(URLConnection.guessContentTypeFromName(filename)));
			if(direct) builder = builder.header("Content-Disposition", "attachment;filename=" + name + "." + ext);
			return builder.body(Files.newInputStream(Paths.get(path)).readAllBytes());
		} catch(Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	public void FileUploadAction(User user, HttpServletRequest req, List<String> origins, List<String> thumbs, List<String> clips) {
		/*** 다운로드 후 동작 */
	}
	
    public String saveFile(MultipartFile file, String basePath, HttpServletRequest req) throws IOException {
        String ext = getExt(file.getOriginalFilename());
        String randomName = Randomizer.generateString(randomNameLength);
        String fullpath = basePath + "/" + randomName + "." + ext;
        Save(file.getInputStream(), req.getServletContext().getRealPath(fullpath));
        return fullpath;
    }
}

































