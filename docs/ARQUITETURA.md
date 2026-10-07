# Arquitetura do ImageLab

A organização mantém a separação entre interface, coordenação, dados e processamento, mas sem forçar toda a lógica a ficar presa a uma lista fixa de classes.

```text
                         ┌──────────────────────┐
                         │      MainView        │
                         │  JavaFX / interação  │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   MainController     │
                         │ coordena o fluxo     │
                         └───────┬───────┬──────┘
                                 │       │
                    ┌────────────┘       └─────────────┐
                    ▼                                  ▼
          ┌──────────────────┐                ┌──────────────────┐
          │      Model       │                │        UI         │
          │ GrayImage/Kernel │                │ parâmetros/análise│
          │ ProcessingResult │                └──────────────────┘
          └────────┬─────────┘
                   │
                   ▼
          ┌──────────────────┐
          │    Processing    │
          │ intensidade      │
          │ histograma       │
          │ filtros          │
          │ bordas           │
          │ convolução       │
          │ aguçamento       │
          │ dissolve         │
          └────────┬─────────┘
                   │
                   ▼
          ┌──────────────────┐
          │       Util       │
          │ I/O, bordas,     │
          │ pixels           │
          └──────────────────┘
```

## Classes principais

- `GrayImage`: representa a imagem de 8 bits.
- `Kernel`: representa uma máscara e seu offset.
- `ProcessingResult`: permite retornar resultado final e respostas intermediárias.
- `MainController`: coordena ações da interface e chama os algoritmos.
- `OperationCatalog`: centraliza nomes e categorias da interface.
- `OperationParameterPane`: parâmetros de transformações.
- `KernelEditorPane`: edição visual de máscaras.
- `AnalysisPane`: histogramas, textos e respostas intermediárias.
- `processing/*`: algoritmos avaliados.
- `util/*`: leitura/escrita, bordas e utilidades de pixels.

A regra importante é que a interface não implementa os algoritmos avaliados; ela apenas coleta parâmetros, chama o controller e apresenta os resultados.
