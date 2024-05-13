import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import os as os
import shutil as shutil


project_list = ["BOOKKEEPER", "OPENJPA"]
dataset_path_list = []

evaluation_path = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name_1}/Evaluation/{name_2}_Evaluation.csv"
box_output_image_path = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name}/Charts/Box/{imageTitle}.png"
line_output_image_path = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name}/Charts/Line/{imageTitle}.png"
image_directory = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name}/Charts/"

def main() :
    init_directories()
    analyze_all_projects()


def init_directories() :
    for project_name in project_list :
        dataset_path_list.append(evaluation_path.format(name_1 = project_name, name_2 = project_name))

    for project_name in project_list :
        directory = image_directory.format(name = project_name)
        if (os.path.isdir(directory)) :
            shutil.rmtree(path = directory)
        os.mkdir(directory)
        os.mkdir(directory + "/" + "Box")
        os.mkdir(directory + "/" + "Line")

def analyze_all_projects() :
    for project_name in project_list :
        analyze_project(project_name)


def analyze_project(project_name) :

    dataset_path = evaluation_path.format(name_1 = project_name, name_2 = project_name)
    dataset = pd.read_csv(dataset_path)

    versions = dataset["TrainingRelease"].drop_duplicates().values
    classifiers = dataset["ClassifierName"].drop_duplicates().values
    filters = dataset["FilterName"].drop_duplicates().values
    samplers = dataset["SamplerName"].drop_duplicates().values
    sensitive = dataset["SensitiveLearning"].drop_duplicates().values

    for filter in filters :
        for sampler in samplers :
            for is_sensitive in sensitive :
                if (sampler != "NotSet" and is_sensitive) :
                    continue
                box_plot_data(project_name, dataset, classifiers, filter, sampler, is_sensitive)
                line_plot_data(project_name, dataset, versions, classifiers, filter, sampler, is_sensitive)



def line_plot_data(project_name, dataset, version_list, classifier_list, filter, sampler, is_sensitive) :

    figure, axis = plt.subplots(nrows = 1, ncols = 3)
    figure.set_size_inches(16,5)
    figure.subplots_adjust(hspace=0.3)

    title_string = "LinePlot.Filter:{filterName}-Sampler:{samplerName}-IsSensitive:{sensitive}"
    title_string = title_string.format(filter_name = filter, sampler_name = sampler, sensitive = is_sensitive)
    figure.suptitle(title_string)

    image_path = line_output_image_path.format(name = project_name, image_title = title_string)

    for index in range(0, len(classifier_list)) :

        classifier = classifier_list[index]

        data = get_data(dataset, None, classifier, filter, sampler, is_sensitive)

        recall_data = data["Recall"]
        precision_data = data["Precision"]
        roc_data = data["ROC_AUC"]

        axis[0].plot(version_list, recall_data, label = classifier)
        axis[0].set_title("Recall")

        axis[1].plot(version_list, precision_data, label = classifier)
        axis[1].set_title("Precision")

        axis[2].plot(version_list, roc_data, label = classifier)
        axis[2].set_title("ROC_AUC")

    for i in range(0, 3) :
        axis[i].legend()
        axis[i].grid()
        axis[i].set_yticks(np.arange(0,1.1, 0.1))
        axis[i].set_xticks(np.arange(0, len(version_list), 1))


    plt.savefig(image_path)
    #plt.show()



def box_plot_data(project_name, dataset, classifier_list, filter, sampler, cost_sensitive) :
    figure, axis = plt.subplots(2, 2)
    figure.set_size_inches(16,9)
    figure.subplots_adjust(hspace=0.3)

    title_string = "BoxPlot.Filter:{filterName}-Sampler:{samplerName}-IsSensitive:{sensitive}"
    title_string = title_string.format(filterName = filter, samplerName = sampler, sensitive = cost_sensitive)
    figure.suptitle(title_string)


    image_path = box_output_image_path.format(name = project_name, imageTitle = title_string)

    recall_list = []
    precision_list = []
    roc_list = []
    kappa_list = []
    for classifier in classifier_list :

        data = get_data(dataset, None, classifier, filter, sampler, cost_sensitive)

        precision_data = data["Precision"]
        precision_data = precision_data[precision_data.notnull()]

        recall_data = data["Recall"]
        recall_data = recall_data[recall_data.notnull()]

        roc_data = data["ROC_AUC"]
        roc_data = roc_data[roc_data.notnull()]

        kappa_data = data["Kappa"]
        kappa_data = kappa_data[kappa_data.notnull()]

        recall_list.append(recall_data)
        precision_list.append(precision_data)
        roc_list.append(roc_data)
        kappa_list.append(kappa_data)

    for i in range(0,2) :
        for j in range(0,2) :
            axis[i,j].set_xticklabels(classifier_list)
            axis[i,j].set_ylim(-0.1, 1)
            axis[i,j].yaxis.grid()
            axis[i,j].set_yticks(np.arange(0,1.1, 0.1))


    axis[0,0].boxplot(recall_list)
    axis[0,0].set_title("Recall")

    axis[0,1].boxplot(precision_list)
    axis[0,1].set_title("Precision")

    axis[1,0].boxplot(roc_list)
    axis[1,0].set_title("ROC AUC")

    axis[1,1].boxplot(kappa_list)
    axis[1,1].set_title("Kappa")

    plt.savefig(image_path)
    #plt.show()


def get_data(dataset, version, classifier, my_filter, sampler, is_sensitive) :
    filtered_dataset = dataset
    if (version != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["TrainingRelease"] == version)]
    if (classifier != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["ClassifierName"] == classifier)]
    if (my_filter != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["FilterName"] == my_filter)]
    if (sampler != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["SamplerName"] == sampler)]
    if (is_sensitive != None) :
        filtered_dataset = filtered_dataset[filtered_dataset["SensitiveLearning"] == is_sensitive]

    return filtered_dataset



if __name__ == "__main__" :
    main()