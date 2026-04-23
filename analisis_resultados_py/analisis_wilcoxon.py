import pandas as pd
from scipy.stats import wilcoxon

#ruta elisa
df = pd.read_excel('/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/PMR_Experimentacion.xlsx', sheet_name='2-ExecutionData')

# Pivotar para que cada fila sea una instancia con la información de ambos algoritmos
df_pivoted = df.pivot_table(index='Instance', columns= 'Algorithm', values=['Covered-Services(%)', 'Work-Productivity']).dropna()

# Test Wilcoxon 1 Random vs Reference
stat_cs_1, p_value_cs_1 = wilcoxon(df_pivoted[('Covered-Services(%)', 'randomSolver')], df_pivoted[('Covered-Services(%)', 'referenceSolver')], alternative = 'greater')

stat_wp_1, p_value_wp_1 = wilcoxon(df_pivoted[('Work-Productivity', 'randomSolver')], df_pivoted[('Work-Productivity', 'referenceSolver')], alternative = 'greater')

# Resultados

print("Resultados test de Wilcoxon RandomSolver vs ReferenceSolver:\n")
print("Total de instancias analizadas:", len(df_pivoted), "\n")
print(f"Covered Services (%): estadístico={stat_cs_1:.4f}, p-valor={p_value_cs_1:.4f}\n")
print(f"Work Productivity: estadístico={stat_wp_1:.4f}, p-valor={p_value_wp_1:.4f}\n")

# Test Wilcoxon 2 Random vs Compacting

stat_cs_2, p_value_cs_2 = wilcoxon(df_pivoted[('Covered-Services(%)', 'randomSolver')], df_pivoted[('Covered-Services(%)', 'compactingSolver')], alternative = 'greater')
stat_wp_2, p_value_wp_2 = wilcoxon(df_pivoted[('Work-Productivity', 'randomSolver')], df_pivoted[('Work-Productivity', 'compactingSolver')], alternative = 'greater')

# Resultados

print("Resultados test de Wilcoxon RandomSolver vs CompactingSolver:\n")
print("Total de instancias analizadas:", len(df_pivoted), "\n")
print(f"Covered Services (%): estadístico={stat_cs_2:.4f}, p-valor={p_value_cs_2:.4f}\n")
print(f"Work Productivity: estadístico={stat_wp_2:.4f}, p-valor={p_value_wp_2:.4f}\n")