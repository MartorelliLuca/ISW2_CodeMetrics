import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import os as os
import shutil as shutil


project_list = ["BOOKKEEPER", "OPENJPA"]
valid_project_list = []

evaluation_path = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name_1}/Evaluation/{name_2}_Evaluation.csv"
box_output_image_path = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name}/Charts/Box/{imageTitle}.png"
line_output_image_path = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name}/Charts/Line/{imageTitle}.png"
comparison_image_path = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/{name}/Charts/"
image_directory = "/home/lux/Documents/GitHub/ISW2_CodeMetrics//output/{name}/Charts/"

def main() :
    init_directories()
    analyze_all_projetcs()


def init_directories() :
    for project_name in project_list :
        path = evaluation_path.format(name_1 = project_name, name_2 = project_name)
        if (os.path.exists(path)) :
            valid_project_list.append(project_name)
            directory = image_directory.format(name = project_name)
            if (os.path.isdir(directory)) :
                shutil.rmtree(path = directory)
            os.mkdir(directory)
            os.mkdir(directory + "/" + "Box")
            os.mkdir(directory + "/" + "Line")
            os.mkdir(directory + "/" + "Comparison")



def analyze_all_projetcs() :
    for projectName in valid_project_list :
        analyze_project(projectName)


def analyze_project(project_name) :

    dataset_path = evaluation_path.format(name_1 = project_name, name_2 = project_name)
    dataset = pd.read_csv(dataset_path)

    versions = dataset["TrainingRelease"].drop_duplicates().values
    classifiers = dataset["ClassifierName"].drop_duplicates().values
    filters = dataset["Filter"].drop_duplicates().values
    samplers = dataset["Sampler"].drop_duplicates().values
    sensitive = dataset["SensitiveLearning"].drop_duplicates().values

    for filter in filters :
        for sampler in samplers :
            for is_sensitive in sensitive :
                if (sampler != "NotSet" and is_sensitive) :
                    continue

                box_plot_data(project_name, dataset, classifiers, filter, sampler, is_sensitive)
                line_plot_data(project_name, dataset, versions, classifiers, filter, sampler, is_sensitive)

    metric_list = ["Precision", "Recall", "ROC_AUC"]
    for metric in metric_list :
        comparison_box_plot(project_name, dataset, classifiers, filters, samplers, sensitive, metric)


def comparison_box_plot(project_name, dataset, classifier_list, filter_list, sampler_list, sensitive_list, metric) :
    nCols = 0
    for filter in filter_list :
        for sampler in sampler_list :
            for is_sensitive in sensitive_list :

                if (is_sensitive and sampler != "NotSet") :
                    continue

                nCols = nCols + 1

    figure, axis = plt.subplots(nrows = 1, ncols = nCols, sharey = "row")
    figure.set_size_inches(20,10)
    figure.set_tight_layout(tight = {"w_pad" : -0.75})
    #figure.subplots_adjust(wspace = 0)

    index = 0
    for filter in filter_list :
        for sampler in sampler_list :
            for is_sensitive in sensitive_list :

                if (is_sensitive and sampler != "NotSet") :
                    continue

                precision_data_list = []

                for classifier in classifier_list :
                    precision_data = get_data(dataset, None, classifier, filter, sampler, is_sensitive)[metric]
                    precision_data = precision_data[precision_data.notnull()]
                    precision_data_list.append(precision_data)

                axis[index].boxplot(precision_data_list)
                axis[index].set_xticklabels(classifier_list, rotation = 60)
                axis[index].set_ylim(-0, 1)
                axis[index].yaxis.grid(linestyle = '--', linewidth = 0.75)
                axis[index].set_yticks(np.arange(-0.05,1.1, 0.05))
                axis[index].set_title("Filter = " + str(filter) + "\nSampling = " + sampler + "\nSensitive = " + str(is_sensitive))

                index += 1

    output_path = comparison_image_path.format(name = project_name, imageTitle =project_name + "_" + metric + "_Comparison")

    figure.suptitle(project_name + " " + metric + " Comparison")
    figure.savefig(output_path)



def line_plot_data(project_name, dataset, version_list, classifier_list, filter, sampler, is_sensitive) :

    figure, axis = plt.subplots(nrows = 1, ncols = 3)
    figure.set_size_inches(15,5)
    figure.set_tight_layout(tight = {"h_pad" : 0.3})

    title_string = "LinePlot-(Filter = {filterName})-(Sampler = {samplerName})-(IsSensitive = {sensitive})"
    title_string = title_string.format(filterName = filter, samplerName = sampler, sensitive = is_sensitive)
    figure.suptitle(title_string)

    image_path = line_output_image_path.format(name = project_name, imageTitle = title_string)

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
        axis[i].legend(loc = 'upper left')
        axis[i].grid()
        axis[i].set_yticks(np.arange(0, 1.4, 0.1))
        axis[i].set_xticks(np.arange(0, len(version_list), 1))


    # figure.legend(labels = classifierList)
    figure.savefig(image_path)
    #plt.show()



def box_plot_data(project_name, dataset, classifier_list, filter, sampler, cost_sensitive) :
    figure, axis = plt.subplots(2, 2)
    figure.set_size_inches(9,9)
    #figure.subplots_adjust(hspace=0.3)
    figure.set_tight_layout(tight = {"h_pad" : 0.3})

    title_string = "BoxPlot-(Filter = {filterName})-(Sampler = {samplerName})-(IsSensitive = {sensitive})"
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
            axis[i,j].set_xticklabels(classifier_list, rotation = 15)
            axis[i,j].set_ylim(-0.1, 1)
            axis[i,j].yaxis.grid()
            axis[i,j].set_yticks(np.arange(-0.1,1.1, 0.1))


            if (i == 1 and j == 1) :
                axis[i,j].set_ylim(-0.5, 1)
                axis[i,j].set_yticks(np.arange(-1, 1.1, 0.2))


    axis[0,0].boxplot(recall_list)
    axis[0,0].set_title("Recall")

    axis[0,1].boxplot(precision_list)
    axis[0,1].set_title("Precision")

    axis[1,0].boxplot(roc_list)
    axis[1,0].set_title("ROC AUC")

    axis[1,1].boxplot(kappa_list)
    axis[1,1].set_title("Kappa")

    figure.savefig(image_path)
    #plt.show()


def get_data(dataset, version, classifier, my_filter, sampler, is_sensitive) :
    filtered_dataset = dataset
    if (version != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["TrainingRelease"] == version)]
    if (classifier != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["ClassifierName"] == classifier)]
    if (my_filter != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["Filter"] == my_filter)]
    if (sampler != None) :
        filtered_dataset = filtered_dataset[(filtered_dataset["Sampler"] == sampler)]
    if (is_sensitive != None) :
        filtered_dataset = filtered_dataset[filtered_dataset["SensitiveLearning"] == is_sensitive]

    return filtered_dataset



if __name__ == "__main__" :
    main()