# Guia rápido para arguição e desafio prático

A avaliação pode cobrar fundamento matemático, lógica do código, cálculo manual de pixels, efeito de parâmetros, comparação de métodos, correção de erros e interpretação de resultados.

## O que saber explicar sem consultar

- `GrayImage`: matriz `pixels[y][x]`, 8 bits e [0,255].
- Replicação de bordas: coordenadas fora da imagem usam o pixel válido mais próximo.
- Convolução: soma de `pixel × coeficiente` mais `offset`.
- Saturação: resultado final limitado a [0,255].
- Média: soma / quantidade de pixels.
- Mediana: ordenação + elemento central.
- Sobel: `Gx`, `Gy` e `sqrt(Gx² + Gy²)`.
- Kirsch: oito máscaras + maior resposta.
- Laplaciano: segunda derivada e relação com bordas/aguçamento.
- High boost: `A > 1` reforça detalhes a partir de uma versão suavizada.
- Histograma: contagem de pixels por intensidade.
- Expansão: remapeamento linear do intervalo presente.
- Equalização: transformação baseada na CDF.
- Contraste adaptativo: média/desvio padrão locais, `c` e vizinhança.

## Treinos práticos recomendados

1. Trocar uma vizinhança 3×3 por 5×5 e prever o efeito.
2. Alterar o tratamento de bordas e explicar o que muda nos cantos.
3. Alterar uma máscara de convolução e prever se ela suaviza, aguça, cria relevo ou destaca bordas.
4. Calcular manualmente um pixel de uma convolução 3×3.
5. Explicar por que uma convolução pode produzir valores negativos.
6. Alterar `gamma` de 1 para 0,5 e explicar o efeito.
7. Comparar média e mediana sobre uma imagem com ruído impulsivo.
8. Explicar a diferença entre `Gx`, `Gy` e magnitude no Sobel.
9. Explicar por que o Kirsch precisa de várias direções.
10. Modificar o código de uma operação sem depender de uma biblioteca que já implemente o algoritmo.
