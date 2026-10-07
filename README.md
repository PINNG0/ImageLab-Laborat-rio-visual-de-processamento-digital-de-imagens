# ImageLab

Aplicação desktop em Java 21 + JavaFX para a AV1 de Processamento de Imagens.

## O que o programa implementa

- Entrada convertida para escala de cinza de 8 bits.
- Negativo, limiarização, alargamento de contraste, gamma e logaritmo.
- Histograma, expansão e equalização.
- Média e mediana com vizinhança configurável.
- Contraste adaptativo com `c` e vizinhança `n×n`.
- Roberts, Sobel, Kirsch e Laplaciano, com respostas intermediárias.
- Gx, Gy e magnitude do gradiente para Roberts/Sobel.
- Oito respostas direcionais do Kirsch e resposta final.
- Aguçamento pelo Laplaciano com `c` e `d`.
- High boost com fator `A`.
- Convolução genérica com máscara `n×m` e offset.
- Presets das máscaras obrigatórias de aguçamento, relevo e bordas.
- Tratamento de bordas por replicação e saturação final em `[0,255]`.
- Dissolve cruzado uniforme e não uniforme.
- Modo de análise com informações, histogramas, máscaras, intermediários e leitura de pixels.
- Zoom independente, navegação por arraste e inspeção de pixels nos dois visualizadores.
- Desfazer da última operação e restauração da imagem original.
- Exportação do resultado em PNG.

## Estrutura

```text
ImageLab/
├── pom.xml
├── README.md
├── images/
│   ├── originals/
│   └── results/
└── src/
    ├── main/java/imagelab/
    │   ├── Main.java
    │   ├── controller/
    │   ├── model/
    │   ├── processing/
    │   ├── ui/
    │   └── util/
    └── test/java/imagelab/
```

## Requisitos

- Java 21
- Maven 3.9+

## Executar

Na pasta do projeto:

```bash
mvn clean test
mvn javafx:run
```

## Uso

1. Abra uma imagem.
2. Escolha a operação.
3. Ajuste os parâmetros quando disponíveis.
4. Clique em **Aplicar**.
5. Compare a imagem original e o resultado.
6. Consulte o painel **Modo de análise** para histogramas, máscaras, respostas intermediárias e pixels.
7. Use **Ajustar**, `−` e `+`, ou `Ctrl + roda do mouse` para o zoom.
8. Com zoom maior que 100%, arraste a imagem para navegar.
9. Use **Desfazer** ou **Restaurar** quando necessário.
10. Use **Salvar resultado** para exportar em PNG.

Atalhos: `Ctrl+O` abre, `Ctrl+S` salva, `Ctrl+Z` desfaz e `Enter` aplica.
