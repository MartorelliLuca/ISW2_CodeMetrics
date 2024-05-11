package model.weka;


import weka.filters.Filter;

public class WekaSampler {

    private Filter sampler ;


    public WekaSampler(Filter sampler) {
        this.sampler = sampler ;
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
}
