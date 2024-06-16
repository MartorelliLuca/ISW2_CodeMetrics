import csv
import re

def parse_filename(filename, project):
    if project == 'BOOKKEEPER':
        pattern = r'bookkeeper_(NaiveBayes|IBk|RandomForest)(true|false)(NotSet|SMOTE|Under|Over)(true|false)(\d)\.csv'
    elif project == 'OPENJPA':
        pattern = r'openjpa_(NaiveBayes|IBk|RandomForest)(true|false)(NotSet|SMOTE|Under|Over)(true|false)([0-9]|1[0-6])\.csv'
    else:
        raise ValueError(f"Unknown project: {project}")

    match = re.match(pattern, filename)

    if match:
        classifier_name = match.group(1)
        filter_name = match.group(2)
        sampler_name = match.group(3)
        sensitive_learning = match.group(4)
        training_release = match.group(5)
        return training_release, classifier_name, filter_name, sampler_name, sensitive_learning
    else:
        raise ValueError(f"Filename {filename} does not match expected pattern for project {project}")

def process_csv(input_filepath, output_filepath, project):
    with open(input_filepath, mode='r', newline='') as infile, open(output_filepath, mode='w', newline='') as outfile:
        reader = csv.DictReader(infile)
        fieldnames = ['TrainingRelease', 'ClassifierName', 'FilterName', 'SamplerName', 'SensitiveLearning', 'Npofb20']
        writer = csv.DictWriter(outfile, fieldnames=fieldnames)

        writer.writeheader()

        for row in reader:
            filename = row['Filename']
            npofb20 = row['Npofb20']
            try:
                training_release, classifier_name, filter_name, sampler_name, sensitive_learning = parse_filename(filename, project)
                writer.writerow({
                    'TrainingRelease': training_release,
                    'ClassifierName': classifier_name,
                    'FilterName': filter_name,
                    'SamplerName': sampler_name,
                    'SensitiveLearning': sensitive_learning,
                    'Npofb20': npofb20
                })
            except ValueError as e:
                print(e)

def main():
    # Percorsi dei file per BOOKKEEPER
    bookkeeper_input = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/BOOKKEEPER/AcumeTot/acumeTot.csv"
    bookkeeper_output = "/home/lux/Documents/BOOKKEEPER_Npofb20.csv"

    # Percorsi dei file per OPENJPA
    openjpa_input = "/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/OPENJPA/AcumeTot/acumeTot.csv"
    openjpa_output = "/home/lux/Documents/OPENJPA_Npofb20.csv"

    # Esegui lo script per BOOKKEEPER
    process_csv(bookkeeper_input, bookkeeper_output, 'BOOKKEEPER')

    # Esegui lo script per OPENJPA
    process_csv(openjpa_input, openjpa_output, 'OPENJPA')

if __name__ == "__main__":
    main()
