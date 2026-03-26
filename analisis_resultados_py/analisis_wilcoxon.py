import pandas as pd
from scipy.stats import wilcoxon

#ruta elisa
df = pd.read_excel('/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/PMR_Experimentacion.xlsx', sheet_name='ExecutionData')

# Pivotar para que cada fila sea una instancia con la información de ambos algoritmos
df_pivoted = df.pivot_table(index='Instance', columns= 'Algorithm', values=['Covered-Services(%)', 'Work-Productivity']).dropna()

# Separar en 2 tablas, una por algoritmo
df_random = df[df['Algorithm'] == 'randomSolver'].set_index('Instance')
df_reference = df[df['Algorithm'] == 'referenceSolver'].set_index('Instance')

# Join de ambas a traves de las instancias
df_joined = df_random.join(df_reference, lsuffix='_random', rsuffix='_reference', how='inner')

covered_services_random = df_joined['Covered-Services(%)_random']
covered_services_reference = df_joined['Covered-Services(%)_reference']

wp_random = df_joined['Work-Productivity_random']
wp_reference = df_joined['Work-Productivity_reference']

# Test de wilcoxon

stat_cs, p_value_cs = wilcoxon(covered_services_random, covered_services_reference, alternative='greater')
stat_wp, p_value_wp = wilcoxon(wp_random, wp_reference, alternative='greater')

# Resultados

print("Resultados test de Wilcoxon:\n")
print("Total de instancias analizadas:", len(df_joined), "\n")
print(f"Covered Services (%): estadístico={stat_cs:.4f}, p-valor={p_value_cs:.4f}\n")
print(f"Work Productivity: estadístico={stat_wp:.4f}, p-valor={p_value_wp:.4f}\n")