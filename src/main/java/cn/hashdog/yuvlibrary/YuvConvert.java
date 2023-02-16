package cn.hashdog.yuvlibrary;

import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * <pre>
 *     library测试类
 * </pre>
 * Created by hyj on 2018/12/18 11:44
 */
public class YuvConvert {
    /**
     * <pre>
     *     将argb的bitmap解成i420的byte[]数据
     *     bitmap的像素解析直接在C里面处理，可以提高效率
     * </pre>
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2i420WithC(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] dst = new byte[width * height * 3 / 2];
        String status = YuvUtils.bitmap2i420WithC(dst, bitmap, width, height);
        if (!TextUtils.isEmpty(status)) {
            dst = null;
            Log.e("bitmap2i420WithC=>", status);
        }
        return dst;
    }

    /**
     * <pre>
     *     将一个bitmap数据转换成为byte[]数据
     *     注意：
     *     这个方法有机率在执行bitmap.copyPixelsToBuffer(buf);的时候发生ndk底层的错误
     *     通过add2line、ndk-stack方法获取错误日志信息:
     *          出现StackOverflowError、backtrace等错误栈信息，具体的日志信息需要根据拿huawei meta 10手机调试抓取
     *     除了这个问题之外，其他的颜色、速度都满足要求，这个ndk的错误未定位到错误点，不能上线
     * </pre>
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2i420(Bitmap bitmap) {
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            //将位图资源转为二进制数据，数据大小为w*h*4
//            int bytes = bitmap.getByteCount();
            int bytes = getBitmapSize(bitmap);
            ByteBuffer buf = ByteBuffer.allocate(bytes);
            bitmap.copyPixelsToBuffer(buf);
            byte[] byteArray = buf.array();

            byte[] ybuffer = new byte[width * height];//用于保存y分量数据
            byte[] ubuffer = new byte[width * height / 4];//用于保存u分量数据
            byte[] vbuffer = new byte[width * height / 4];//用于保存v分量数据
            //将位图数据argb转换为yuv I420 转换后的数据分别保存在 ybuffer、ubuffer和vbuffer里面
            YuvUtils.argbtoi420(byteArray, width * 4, ybuffer, width, ubuffer, (width + 1) / 2, vbuffer, (width + 1) / 2, width, height);

            //将上面的yuv数据保存到一个数组里面组成一帧yuv I420 数据 分辨率为w*h
            byte[] frameBuffer = new byte[width * height * 3 / 2];
            System.arraycopy(ybuffer, 0, frameBuffer, 0, width * height);
            System.arraycopy(ubuffer, 0, frameBuffer, width * height, width * height / 4);
            System.arraycopy(vbuffer, 0, frameBuffer, width * height * 5 / 4, width * height / 4);
            return frameBuffer;
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取图片的大小
     *
     * @param bitmap
     * @return
     */
    private static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //SInce API 19
            return bitmap.getAllocationByteCount();
        }

        //Since API 12
        return bitmap.getByteCount();
    }
}