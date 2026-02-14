## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


## Notas de lo de ahora
- TensorCodec (decode/encode) + TensorMath para clamp. (Tú parte, Luis)
- TensorOps (bilinear, convolve, lerp).
- La GUI solo usa: ImageProcessor (y ya conectamos todo con DefaultImageProcessor).


## Conexión

Inyección de `TensorCodec + TensorOps` en `DefaultImageProcessor`

```java
var codec = new com.tensorupscaler.core.SimpleTensorCodec();
var ops   = new com.tensorupscaler.core.SimpleTensorOps();
var proc  = new com.tensorupscaler.core.DefaultImageProcessor(codec, ops);

// kernel recomendado
float[][] k = com.tensorupscaler.core.Kernels.sharpen();

// sharpenAmount típico: 0.4–0.8
BufferedImage out = proc.process(input, 2, 0.6f, k);
```
