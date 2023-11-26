package org.apereo.cas.web.y9.util.common;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ValidatorCodeUtil {
    public static class ValidatorCode {
        private BufferedImage image;
        private String code;

        public String getCode() {
            return code;
        }

        public BufferedImage getImage() {
            return image;
        }
    }

    public static ValidatorCode getCode() {
        // 验证码图片的宽度。
        int width = 80;
        // 验证码图片的高度。
        int height = 26;
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();

        // 创建一个随机数生成器类。
        Random random = new Random();

        // 设定图像背景色(因为是做背景，所以偏淡)
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.HANGING_BASELINE, 20);
        // 设置字体。
        g.setFont(font);

        // 画边框。
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到。
        // g.setColor(Color.GRAY);
        // g.setColor(getRandColor(160, 200));
        // for (int i = 0; i < 155; i++) {
        // int x = random.nextInt(width);
        // int y = random.nextInt(height);
        // int xl = random.nextInt(12);
        // int yl = random.nextInt(12);
        // g.drawLine(x, y, x + xl, y + yl);
        // }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();

        // 设置默认生成4个验证码
        int length = 4;
        // 设置备选验证码:包括"a-z"和数字"0-9"
        // String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String base = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

        int size = base.length();

        // 随机产生4位数字的验证码。
        for (int i = 0; i < length; i++) {
            // 得到随机产生的验证码数字。
            int start = random.nextInt(size);
            String strRand = base.substring(start, start + 1);

            // 用随机产生的颜色将验证码绘制到图像中。
            // 生成随机颜色(因为是做前景，所以偏深)
            // g.setColor(getRandColor(1, 100));

            // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            // g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.setColor(new Color(0, 0, 0));
            g.drawString(strRand, 18 * i + 2, 22);

            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }

        // 图象生效
        g.dispose();
        ValidatorCode code = new ValidatorCode();
        code.image = buffImg;
        code.code = randomCode.toString();
        return code;
    }

    // 给定范围获得随机颜色
    static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}