OxeProv
=======

Sistema de provisionamento para Alcatel OmniPCX.

Informações gerais para desenvolvimento
---------------------------------------

As configurações são lidas pela linha de comando (`-c`) e gravadas em `br.eng.etech.oxeprov.Config.global`, que é uma instância de `br.eng.etech.oxeprov.Config`.

A execução do sistema é pelo `br.eng.etech.oxeprov.FolderMonitor.run()`.

FolderMonitor.run()
-------------------

Este método lê todos os arquivos de comandos da pasta configurada por `folder.search` e cria um `br.eng.etech.oxeprov.Batch` para cada um deles, executando-os pelo método `run()`. No final, ele apaga o arquivo de comandos lido.

Batch.run()
-----------

Este método lê cada linha do arquivo de comandos e cria um objeto de comando representado por `br.eng.etech.oxeprov.Mgr.Command`.

Em seguida, carrega, do poll de conexões, a conexão relativo OmniPCX onde o comando deverá ser executado e envia o comando para esta conexão específica. O resultado é interpretado e gravado em um arquivo gerado na pasta configurada por `folder.output`.

O método `toString()` do `Mgr.Command` é o responsável por criar o texto para ser interpretado pelo OmniPCX.
 
