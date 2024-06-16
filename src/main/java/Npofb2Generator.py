import csv

def read_csv_as_dict(file_path, key_columns):
    data_dict = {}
    with open(file_path, mode='r', newline='') as file:
        reader = csv.DictReader(file)
        for row in reader:
            key = tuple(row[column] for column in key_columns)
            data_dict[key] = row
    return data_dict

def write_combined_csv(file2_path, file1_data, output_path, key_columns):
    with open(file2_path, mode='r', newline='') as file2, open(output_path, mode='w', newline='') as output_file:
        reader = csv.DictReader(file2)
        fieldnames = reader.fieldnames + ['Npofb20']
        writer = csv.DictWriter(output_file, fieldnames=fieldnames)
        writer.writeheader()

        for row in reader:
            key = tuple(row[column] for column in key_columns)
            if key in file1_data:
                row['Npofb20'] = file1_data[key]['Npofb20']
            else:
                row['Npofb20'] = None  # or some default value
            writer.writerow(row)

def main():
    # FILE PATHS FOR BOOKKEEPER
    bookkeeper_file1_path = '/home/lux/Documents/BOOKKEEPER_Npofb20.csv'
    bookkeeper_file2_path = '/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/BOOKKEEPER/Evaluation/BOOKKEEPER_Evaluation.csv'
    bookkeeper_output_path = '/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/BOOKKEEPER/Evaluation/BOOKKEEPER_TOTAL_Evaluation.csv'

    # FILE PATHS FOR OPENJPA
    openjpa_file1_path = '/home/lux/Documents/OPENJPA_Npofb20.csv'
    openjpa_file2_path = '/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/OPENJPA/Evaluation/OPENJPA_Evaluation.csv'
    openjpa_output_path = '/home/lux/Documents/GitHub/ISW2_CodeMetrics/output/OPENJPA/Evaluation/OPENJPA_TOTAL_Evaluation.csv'

    key_columns = ['TrainingRelease', 'ClassifierName', 'FilterName', 'SamplerName', 'SensitiveLearning']

    # Process BOOKKEEPER files
    bookkeeper_file1_data = read_csv_as_dict(bookkeeper_file1_path, key_columns)
    write_combined_csv(bookkeeper_file2_path, bookkeeper_file1_data, bookkeeper_output_path, key_columns)

    # Process OPENJPA files
    openjpa_file1_data = read_csv_as_dict(openjpa_file1_path, key_columns)
    write_combined_csv(openjpa_file2_path, openjpa_file1_data, openjpa_output_path, key_columns)

if __name__ == "__main__":
    main()
