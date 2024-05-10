package model.weka;


import weka.filters.Filter;

public class WekaSampler {

    private Filter sampler ;
    private String samplerName ;


    public WekaSampler(Filter sampler, String samplerName) {
        this.sampler = sampler ;
        this.samplerName = samplerName ;
    }

    public Filter getSampler() {
        return sampler;
    }

    public void setSampler(Filter sampler) {
        this.sampler = sampler;
    }

    public String getSamplerName() {
        return samplerName;
    }

    public void setSamplerName(String samplerName) {
        this.samplerName = samplerName;
    }
}
