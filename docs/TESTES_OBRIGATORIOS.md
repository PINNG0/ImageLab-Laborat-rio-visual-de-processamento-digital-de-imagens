# Testes obrigatórios de convolução

Todos os casos abaixo utilizam a mesma imagem-base (`images/originals/lena_gray_256.tif`) para facilitar a comparação.

## Tratamento comum

- Bordas: replicação do pixel mais próximo.
- Resultado final: saturação para [0,255].
- Operação: `g = f*h + offset`.
- Offset dos testes: 0, salvo indicação diferente.

## 1. Filtro de aguçamento

Máscara:

```text
[-c   -c       -c]
[-c  8c+d      -c]
[-c   -c       -c]
```

| Teste | c | d | Soma dos coeficientes | Resultado |
|---|---:|---:|---:|---|
| A | 1 | 1 | 1 | `images/results/17_aguçamento_c1_d1.png` |
| B | 2 | 2 | 2 | `images/results/17_aguçamento_c2_d2.png` |
| C | 1 | 2 | 2 | `images/results/17_aguçamento_c1_d2.png` |

**Interpretação:** aumentar `c` aumenta a contribuição dos coeficientes negativos e do termo central `8c+d`, reforçando variações locais. O parâmetro `d` altera a soma dos coeficientes e, portanto, o ganho global da máscara.

## 2. Filtros de relevo

### h1

```text
[ 0  0  0]
[ 0  1  0]
[ 0  0 -1]
```

Resultado: `images/results/18_relevo_h1.png`

### h2

```text
[ 0  0  2]
[ 0 -1  0]
[-1  0  0]
```

Resultado: `images/results/19_relevo_h2.png`

**Interpretação:** as posições dos coeficientes positivos e negativos definem quais lados das estruturas são realçados, alterando a direção aparente do relevo.

## 3. Máscaras de detecção de bordas

### h3

```text
[-1 -1  0]
[ 1  0  1]
[ 0  1  1]
```

Resultado: `images/results/20_bordas_h3.png`

### h4

```text
[ 0 -1  0]
[-1 -4 -1]
[ 0 -1  0]
```

Resultado: `images/results/21_bordas_h4.png`

**Interpretação:** sinais opostos e posições diferentes fazem a máscara responder a diferentes variações espaciais. Como as respostas podem ser negativas, parte dos valores pode ser saturada em 0 na imagem final.
