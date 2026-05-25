import pandas as pd
from scipy.stats import wilcoxon
from scipy.stats import rankdata

#ruta elisa
df = pd.read_excel('/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/PMR_Experimentacion.xlsx', sheet_name='2-ExecutionData')

# Pivotar para que cada fila sea una instancia con la información de ambos algoritmos
df_pivoted = df.pivot_table(index='Instance', columns= 'Algorithm', values=['Covered-Services(%)', 'Work-Productivity']).dropna()

alpha = 0.05
num_contrastes = 4
alpha_corr = alpha / num_contrastes

def efecto_biserial(x,y):  # Calcula el efecto biserial. 
    # Resumidamente, significa que si el resultado es positivo, 
    # el primer algoritmo es mejor, y si es negativo, el segundo algoritmo es mejor. 
    # El valor absoluto indica la magnitud de la diferencia.
    diff = x - y
    diff = diff[diff != 0]  # Eliminar ceros
    
    ranks = rankdata(abs(diff))
    w_pos = ranks[diff > 0].sum()
    w_neg = ranks[diff < 0].sum()
    
    return (w_pos - w_neg) / (w_pos + w_neg)

print("Nivel de significancia corregido:", alpha_corr, "\n")

# Test Wilcoxon 1 Random vs Reference
stat_cs_1, p_value_cs_1 = wilcoxon(df_pivoted[('Covered-Services(%)', 'randomSolver')], df_pivoted[('Covered-Services(%)', 'referenceSolver')], alternative = 'greater')
r_cs_1 = efecto_biserial(df_pivoted[('Covered-Services(%)', 'randomSolver')], df_pivoted[('Covered-Services(%)', 'referenceSolver')])

stat_wp_1, p_value_wp_1 = wilcoxon(df_pivoted[('Work-Productivity', 'randomSolver')], df_pivoted[('Work-Productivity', 'referenceSolver')], alternative = 'greater')
r_wp_1 = efecto_biserial(df_pivoted[('Work-Productivity', 'randomSolver')], df_pivoted[('Work-Productivity', 'referenceSolver')])

# Resultados

print("Resultados test de Wilcoxon RandomSolver vs ReferenceSolver:\n")
print("Total de instancias analizadas:", len(df_pivoted), "\n")
print(f"Covered Services (%): estadístico={stat_cs_1:.4f}, p-valor={p_value_cs_1:.2e}, efecto biserial={r_cs_1:.4f}, p-valor corregido={min(p_value_cs_1 * num_contrastes, 1):.2e}\n")
print(f"Work Productivity: estadístico={stat_wp_1:.4f}, p-valor={p_value_wp_1:.2e}, efecto biserial={r_wp_1:.4f}, p-valor corregido={min(p_value_wp_1 * num_contrastes, 1):.2e}\n")

# Test Wilcoxon 2 Random vs Compacting

stat_cs_2, p_value_cs_2 = wilcoxon(df_pivoted[('Covered-Services(%)', 'compactingSolver')], df_pivoted[('Covered-Services(%)', 'randomSolver')], alternative = 'greater')
r_cs_2 = efecto_biserial(df_pivoted[('Covered-Services(%)', 'compactingSolver')], df_pivoted[('Covered-Services(%)', 'randomSolver')])

stat_wp_2, p_value_wp_2 = wilcoxon(df_pivoted[('Work-Productivity', 'compactingSolver')], df_pivoted[('Work-Productivity', 'randomSolver')], alternative = 'greater')
r_wp_2 = efecto_biserial(df_pivoted[('Work-Productivity', 'compactingSolver')], df_pivoted[('Work-Productivity', 'randomSolver')])

# Resultados

print("Resultados test de Wilcoxon CompactingSolver vs RandomSolver:\n")
print("Total de instancias analizadas:", len(df_pivoted), "\n")
print(f"Covered Services (%): estadístico={stat_cs_2:.4f}, p-valor={p_value_cs_2:.2e}, efecto biserial={r_cs_2:.4f}, p-valor corregido={min(p_value_cs_2 * num_contrastes, 1):.2e}\n")
print(f"Work Productivity: estadístico={stat_wp_2:.4f}, p-valor={p_value_wp_2:.2e}, efecto biserial={r_wp_2:.4f}, p-valor corregido={min(p_value_wp_2 * num_contrastes, 1):.2e}\n")