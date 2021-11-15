package com.ghf.exchange.boss.common.validatecode.util;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author: ouyang
 * @Date: 2021/8/31 上午9:40
 * @Description: 滑块验证工具
 */
@Slf4j
public class VerifyImageUtil {

    /**
     * 小图长
     */
    public static final int TARGET_WIDTH = 55;

    /**
     * 小图宽
     */
    public static final int TARGET_HEIGHT = 45;

    /**
     * 半径
     */
    public static final int CIRCLE_R = 8;

    /**
     * 距离点
     */
    public static final int R1 = 4;

    /**
     * 生成小图轮廓
     *
     * @return
     */
    public static int[][] getBlockData() {
        int[][] data = new int[TARGET_WIDTH][TARGET_HEIGHT];
        //47
        double x2 = TARGET_WIDTH - CIRCLE_R;

        //随机生成圆的位置
        double h1 = CIRCLE_R + Math.random() * (TARGET_WIDTH - 3 * CIRCLE_R - R1);
        //64
        double po = Math.pow(CIRCLE_R, 2);

        double xbegin = TARGET_WIDTH - CIRCLE_R - R1;
        double ybegin = TARGET_HEIGHT - CIRCLE_R - R1;

        //圆的标准方程 (x-a)²+(y-b)²=r²,标识圆心（a,b）,半径为r的圆
        //计算需要的小图轮廓，用二维数组来表示，二维数组有两张值，0和1，其中0表示没有颜色，1有颜色
        for (int i = 0; i < TARGET_WIDTH; i++) {
            for (int j = 0; j < TARGET_HEIGHT; j++) {
                double d2 = Math.pow(j - 2, 2) + Math.pow(i - h1, 2);
                double d3 = Math.pow(i - x2, 2) + Math.pow(j - h1, 2);
                boolean b = (j <= ybegin && d2 < po) || (i >= xbegin && d3 > po);
                if (b) {
                    data[i][j] = 0;
                } else {
                    data[i][j] = 1;
                }
            }
        }
        return data;
    }

    /**
     * 有这个轮廓后就可以依据这个二维数组的值来判定抠图并在原图上抠图位置处加阴影,
     *
     * @param oriImage      原图
     * @param targetImage   抠图拼图
     * @param templateImage 颜色
     * @param x
     * @param y
     */
    public static void cutByTemplate(BufferedImage oriImage, BufferedImage targetImage, int[][] templateImage, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        //创建shape区域
        for (int i = 0; i < TARGET_WIDTH; i++) {
            for (int j = 0; j < TARGET_HEIGHT; j++) {
                int rgb = templateImage[i][j];
                // 原图中对应位置变色处理
                int rgbOri = oriImage.getRGB(x + i, y + j);

                if (rgb == 1) {
                    targetImage.setRGB(i, j, rgbOri);

                    //抠图区域高斯模糊
                    readPixel(oriImage, x + i, y + j, values);
                    fillMatrix(martrix, values);
                    oriImage.setRGB(x + i, y + j, avgMatrix(martrix));
                } else {
                    //这里把背景设为透明
                    targetImage.setRGB(i, j, rgbOri & 0x00ffffff);
                }
            }
        }
    }

    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        int number3 = 3;
        for (int i = xStart; i < number3 + xStart; i++) {
            for (int j = yStart; j < number3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;

                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);

            }
        }
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }

}
