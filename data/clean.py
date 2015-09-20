import os

import pandas as pd
import numpy as np

def load(filename):
    records = []
    with open(filename, 'r') as f:
        for line in f:
            hours_slept = line[282:284].strip()
            if hours_slept:
                record = {
                    'hours_slept': int(hours_slept),
                    'state': line[0:2],
                    'geographic_stratum': line[2:4].strip(),
                    'county': line[134:137].strip(),
                    'gender': int(line[145]),
                    'general_health': int(line[72].strip()) if line[72].strip() else np.nan,
                    'days_not_enough_sleep': int(line[82:85].strip()) if line[82:85].strip() else np.nan,
                    'education': int(line[117]) if line[117].strip() else np.nan,
                    'employment': int(line[118]) if line[118].strip() else np.nan,
                    'income': int(line[119-121]) if line[119-121].strip() else np.nan,
                    'life_satisfaction': int(line[224]) if line[224].strip() else np.nan
                }
                records.append(record)

    data_frame = pd.DataFrame(records)
    return data_frame

if __name__ == '__main__':
    df = load('CDBRFS09.ASC')
    df.to_csv('processed_CDBRFS09.csv')
