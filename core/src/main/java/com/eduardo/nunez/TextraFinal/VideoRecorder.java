package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class VideoRecorder {
    private Process ffmpegProcess;
    private OutputStream outputStream;
    private boolean isRecording = false;
    private int width;
    private int height;

    public void startRecording(String outputFileName, String audioPath, int width, int height) {
        if (isRecording)
            return;
        this.width = width;
        this.height = height;

        String ffmpegCmd = "ffmpeg";
        // Arguments breakdown:
        // -y : Overwrite output file
        // -f rawvideo : Input format is raw video
        // -pix_fmt rgba : Pixel format from LibGDX (RGBA)
        // -s widthxheight : Resolution
        // -r 60 : Framerate (assumed 60)
        // -i - : Input from pipe (stdin)
        // -i audioPath : Input audio file (muxing it in)
        // -map 0:v:0 : Map video from input 0
        // -map 1:a:0 : Map audio from input 1
        // -c:v libx264 : Video codec H.264
        // -preset ultrafast : Fast encoding to prevent lag
        // -c:a aac : Audio codec
        // -shortest : Finish when the shortest stream ends (usually video if we stop
        // early)

        // Note: We need to handle file paths carefully in commands
        String safeAudioPath = audioPath.replace("/", "\\");

        String[] command = {
                ffmpegCmd,
                "-y",
                "-f", "rawvideo",
                "-pix_fmt", "rgba",
                "-s", width + "x" + height,
                "-r", "60",
                "-i", "-",
                "-i", safeAudioPath,
                "-map", "0:v:0",
                "-map", "1:a:0",
                "-pix_fmt", "yuv420p",
                "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2", // Ensure even dimensions for yuv420p
                "-c:v", "libx264",
                "-preset", "ultrafast",
                "-crf", "17", // High quality
                "-c:a", "aac",
                "-shortest",
                outputFileName
        };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // Merge stderr into stdout

            ffmpegProcess = pb.start();
            outputStream = ffmpegProcess.getOutputStream();
            isRecording = true;
            Gdx.app.log("RECORDER", "Started recording to " + outputFileName);

            // Consume and log FFmpeg output
            new Thread(() -> {
                try {
                    java.io.InputStream is = ffmpegProcess.getInputStream();
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Gdx.app.log("FFMPEG", line); // Comment in to see full ffmpeg log
                        // Only log errors or warnings to avoid spam, or log everything if debugging
                        System.out.println("[FFMPEG] " + line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            Gdx.app.error("RECORDER", "Failed to start FFmpeg", e);
            isRecording = false;
        }
    }

    public void captureFrame() {
        if (!isRecording || outputStream == null)
            return;

        try {
            // Get pixels from screen
            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);

            // Write to FFmpeg
            outputStream.write(pixels);
            outputStream.flush();

        } catch (Exception e) {
            Gdx.app.error("RECORDER", "Error writing frame", e);
            stopRecording();
        }
    }

    public void stopRecording() {
        if (!isRecording)
            return;

        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (ffmpegProcess != null) {
                ffmpegProcess.waitFor();
                ffmpegProcess.destroy();
            }
        } catch (Exception e) {
            Gdx.app.error("RECORDER", "Error stopping recording", e);
        }

        isRecording = false;
        Gdx.app.log("RECORDER", "Stopped recording");
    }

    public boolean isRecording() {
        return isRecording;
    }
}
