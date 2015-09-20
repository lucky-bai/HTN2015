import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd


def pairwise():
    data = pd.read_csv('processed_CDBRFS09.csv')
    g = sns.PairGrid(data, hue='hours_slept', vars=['age', 'hours_slept', 'general_health', 'life_satisfaction'])
    g = g.map(plt.scatter)
    g = g.add_legend()
    plt.savefig('pairplot.png')

if __name__ == '__main__':
    pairwise()