# Questões de domínio conceitual — AV1

## 1. Transformação pontual × operação baseada em vizinhança

Uma transformação pontual calcula a saída de um pixel usando essencialmente o valor daquele mesmo pixel. É o caso do negativo, da limiarização, da transformação gamma e do logaritmo.

Uma operação baseada em vizinhança usa vários pixels ao redor da posição analisada. É o caso da média, mediana, convolução, Sobel, Roberts, Kirsch, Laplaciano e contraste adaptativo. Portanto, a diferença principal está na quantidade de informação espacial utilizada para calcular cada saída.

## 2. Transformação gamma

Na forma utilizada pelo sistema, a transformação é `s = c · r^γ`, com a intensidade normalizada para o intervalo [0,1] durante o cálculo.

- `γ < 1`: tende a ampliar as intensidades baixas e clarear regiões escuras.
- `γ = 1`: com `c = 1`, mantém a intensidade original.
- `γ > 1`: tende a reduzir as intensidades baixas e escurecer a imagem.

Assim, se uma imagem predominantemente escura passar de `γ = 1` para `γ = 0,5`, espera-se que ela fique visualmente mais clara, principalmente nas regiões de baixa intensidade.

## 3. Média × mediana

O filtro da média é linear: soma os valores da vizinhança e divide pela quantidade de pixels. Ele suaviza a imagem, mas também pode espalhar valores extremos.

O filtro da mediana é não linear: ordena os valores da vizinhança e escolhe o elemento central. Por isso, costuma ser mais eficiente contra ruído impulsivo, como pontos isolados muito claros ou muito escuros, preservando melhor as bordas.

## 4. Gx e Gy no Sobel

`Gx` representa a resposta da máscara orientada para detectar variações na direção horizontal; `Gy` representa a resposta complementar. As duas respostas preservam informações diferentes sobre a orientação das bordas.

A magnitude do gradiente é calculada combinando as duas componentes, no projeto por:

`M = √(Gx² + Gy²)`

Depois, o resultado é limitado ao intervalo [0,255] para exibição.

## 5. Roberts × Sobel × Kirsch × Laplaciano

**Roberts:** utiliza máscaras pequenas de 2×2 e responde rapidamente a variações locais diagonais, sendo mais sensível a ruído.

**Sobel:** utiliza máscaras 3×3 para `Gx` e `Gy`, permitindo obter magnitude e orientação do gradiente. A vizinhança maior tende a produzir uma resposta mais estável que Roberts.

**Kirsch:** utiliza oito máscaras direcionais 3×3. Cada máscara procura uma orientação específica e a resposta final é formada a partir da maior resposta direcional.

**Laplaciano:** é um operador de segunda derivada, normalmente isotrópico quando utilizada uma máscara simétrica. Evidencia mudanças rápidas de intensidade, mas pode ser sensível ao ruído.

## 6. Por que o Kirsch usa várias orientações?

Uma única máscara favoreceria determinadas orientações. As oito máscaras permitem verificar várias direções possíveis de borda. Para cada pixel, selecionar a maior resposta permite conservar a orientação que produziu a evidência mais forte de borda naquele ponto.

## 7. Laplaciano para bordas e aguçamento

O Laplaciano evidencia regiões em que a intensidade muda rapidamente, portanto pode ser usado para detectar bordas.

No aguçamento, a resposta do Laplaciano é combinada com a imagem original para reforçar essas variações. O sinal da combinação depende da convenção utilizada para a máscara do Laplaciano. Com a máscara implementada no ImageLab, o aguçamento usa a forma `original - Laplaciano`, enquanto outras convenções podem exigir soma.

## 8. Valores fora de [0,255]

Uma convolução combina pixels com coeficientes positivos e negativos. Por isso, a soma pode ser negativa ou maior que 255 mesmo quando todos os pixels originais estão em [0,255].

O ImageLab mantém o cálculo intermediário como `double` quando necessário e aplica saturação no resultado final: valores menores que 0 tornam-se 0 e valores maiores que 255 tornam-se 255.

Converter diretamente um valor fora dessa faixa para um inteiro de 8 bits sem tratamento pode causar truncamento, overflow ou interpretação incorreta da intensidade.

## 9. Expansão × equalização de histograma

A **expansão** usa os níveis mínimo e máximo presentes e os remapeia linearmente para uma faixa maior, normalmente de 0 a 255. É uma transformação linear baseada nos extremos da imagem.

A **equalização** utiliza a distribuição acumulada do histograma para redistribuir as intensidades. Seu objetivo é ocupar melhor a faixa dinâmica de acordo com a distribuição dos pixels.

A equalização não necessariamente produz uma imagem visualmente melhor. O resultado depende da imagem e do objetivo: ela pode aumentar o contraste de algumas regiões, mas também pode produzir uma aparência exagerada ou destacar ruído.

## 10. Aguçamento × detecção de bordas

Detecção de bordas procura destacar mudanças de intensidade para localizar contornos e estruturas. O resultado normalmente é uma representação das bordas, e não uma imagem fotográfica preservada.

Aguçamento utiliza informações de alta frequência para reforçar detalhes na própria imagem original. Portanto, uma imagem produzida por um operador de bordas não é automaticamente uma imagem aguçada: uma destaca estruturas para análise, enquanto a outra combina essas estruturas com a imagem original para aumentar a nitidez.
