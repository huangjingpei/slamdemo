package com.hiscene.hiarslamdemo.bean;

/**
 * Created by lichong on 2018/10/23.
 *
 * @ Email lichongmac@163.com
 */

import java.io.Serializable;

/**
 * update:2018/10/23
 * updated by lic@hiscene.com
 * 说明：
 * 1)边缘检测参数配置类
 * 2）Builder模式构建
 * 3）设定默认值返回
 */
public class EdgeTrackerMePara implements Serializable{

    private String name;//模型参数配置名称

    private int maskSize;      //初始默认5 

    private int maskNumber;    //初始默认180 
    private int range;         //初始默认8
    private int threshold;     //初始默认90000 
    private double mu1;         //初始默认0.5 
    private double mu2;         //初始默认1.5 
    private int sampleStep;    //初始默认10 
    private double percentageGd;    //初始默认0.6
    private double projErrorThreshold;   // 8.5

    public EdgeTrackerMePara(Builder builder) {
        this.name=builder.name;
        this.maskSize = builder.maskSize;
        this.maskNumber = builder.maskNumber;
        this.range = builder.range;
        this.threshold = builder.threshold;
        this.mu1 = builder.mu1;
        this.mu2 = builder.mu2;
        this.sampleStep = builder.sampleStep;
        this.percentageGd=builder.percentageGd;
        this.projErrorThreshold=builder.projErrorThreshold;
    }

    public String getName() {
        return name;
    }

    public double getPercentagePet() {
        return projErrorThreshold;
    }

    public double getPercentageGd() {
        return percentageGd;
    }

    public int getMaskSize() {
        return maskSize;
    }

    public int getMaskNumber() {
        return maskNumber;
    }

    public int getRange() {
        return range;
    }

    public int getThreshold() {
        return threshold;
    }

    public double getMu1() {
        return mu1;
    }

    public double getMu2() {
        return mu2;
    }

    public int getSampleStep() {
        return sampleStep;
    }

    public static class Builder {
        private String name;//模型参数配置名称
        private int maskSize = 5;      //初始默认5 

        private int maskNumber = 180;    //初始默认180 
        private int range = 8;         //初始默认8
        private int threshold = 90000;     //初始默认90000 
        private double mu1 = 0.5f;         //初始默认0.5 
        private double mu2 = 1.5f;         //初始默认1.5 
        private int sampleStep = 10;    //初始默认10 
        private double percentageGd = 0.6;    //初始默认0.6
        private double projErrorThreshold = 8.5;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setMaskSize(int maskSize) {
            this.maskSize = maskSize;

            return this;
        }

        public Builder setMaskNumber(int maskNumber) {
            this.maskNumber = maskNumber;
            return this;

        }

        public Builder setRange(int range) {
            this.range = range;
            return this;

        }

        public Builder setThreshold(int threshold) {
            this.threshold = threshold;
            return this;

        }

        public Builder setMu1(double mu1) {
            this.mu1 = mu1;
            return this;

        }

        public Builder setMu2(double mu2) {
            this.mu2 = mu2;
            return this;

        }

        public Builder setSampleStep(int sampleStep) {
            this.sampleStep = sampleStep;
            return this;

        }

        public Builder setPercentageGd(double percentageGd) {
            this.percentageGd = percentageGd;
            return this;
        }

        public Builder setPercentagePet(double percentagePet) {
            this.projErrorThreshold = percentagePet;
            return this;
        }

        public EdgeTrackerMePara build() {
            return new EdgeTrackerMePara(this);
        }


    }
}
