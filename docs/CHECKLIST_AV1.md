# Checklist de entrega — ImageLab / AV1 2026

Este checklist acompanha os requisitos consolidados da AV1 de Processamento de Imagens da UNIR.

| Requisito | Situação no projeto |
|---|---|
| Java 21 + Maven | ✅ Configurado no `pom.xml` |
| JavaFX | ✅ |
| Imagem em escala de cinza 8 bits | ✅ `GrayImage` |
| Faixa final [0,255] | ✅ clamp centralizado |
| Tratamento de bordas | ✅ replicação |
| Dissolve uniforme | ✅ |
| Dissolve não uniforme | ✅ máscara de pesos |
| Negativo | ✅ |
| Limiarização | ✅ parametrizada |
| Alargamento de contraste | ✅ parametrizado |
| Gamma | ✅ parametrizado |
| Logaritmo | ✅ parametrizado |
| Histograma | ✅ operação + modo análise |
| Expansão de histograma | ✅ |
| Equalização | ✅ |
| Média | ✅ vizinhança parametrizável |
| Mediana | ✅ vizinhança parametrizável |
| Roberts | ✅ Gx, Gy e magnitude |
| Sobel | ✅ Gx, Gy, magnitude e resultado final |
| Kirsch | ✅ 8 respostas direcionais + final |
| Laplaciano | ✅ resposta final |
| Aguçamento pelo Laplaciano | ✅ resposta intermediária + resultado |
| High boost | ✅ fator A parametrizável |
| Contraste adaptativo | ✅ c + n×n parametrizáveis |
| Convolução genérica | ✅ máscara n×m + offset |
| Valores fora de [0,255] | ✅ saturação para [0,255] |
| Teste c=d=1 | ✅ preset + teste |
| Outro teste de c,d | ✅ presets c=2,d=2 e c=1,d=2 |
| Relevo h1/h2 | ✅ presets + resultados |
| Bordas h3/h4 | ✅ presets + resultados |
| Interface | ✅ versão revisada |
| Modo análise | ✅ texto, histograma e intermediárias |
| Imagens/resultados | ✅ conjunto inicial em `images/results/` |
| Questões conceituais | ✅ `docs/QUESTOES_CONCEITUAIS.md` |
| Declaração de IA | ✅ `docs/DECLARACAO_IA.md` |
| Relatório | 🟡 base pronta; preencher identificação e captura final da interface |
| Referências | 🟡 seção preparada no relatório |
| GitHub final | 🟡 depende do repositório do aluno |
| Arguição | 🟡 material de revisão em `docs/GUIA_ARGUICAO.md` |
| Desafio prático | 🟡 material de revisão em `docs/GUIA_ARGUICAO.md` |


