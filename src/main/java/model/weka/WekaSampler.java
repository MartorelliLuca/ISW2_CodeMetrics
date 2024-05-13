package model.weka;


import weka.filters.Filter;


public class WekaSampler {

    private Filter sampler ;
    private String samplingType ;


    public WekaSampler(Filter sampler, String samplingType) {
        this.sampler = sampler ;
        this.samplingType = samplingType ;
    }

    public Filter getSampler() {
        return sampler;
    }

    public void setSampler(Filter sampler) {
        this.sampler = sampler;
    }

    public String getSamplerName() {
        return sampler.getClass().getSimpleName() ;
    }

    public String getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(String samplingType) {
        this.samplingType = samplingType;
    }
}