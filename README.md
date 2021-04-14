# HybridSynchAADL

HybridSynchAADL (Hybrid Synchronous AADL) is a verification tool which presents
HybridSynchAADL modeling language and formal analysis for virtually synchronous
cyber-physical systems with complex control programs, continuous behaviors, and
bounded clock skews, network delays, and execution times.


HybridSynchAADL models are given a formal semantics and analyzed using Maude
with SMT solving, which allows us to represent advanced control programs and
communication features in Maude, while capturing timing uncertainties and
continuous behaviors symbolically with SMT solving. 


## Installation  

HybridSynchAADL tool is an [OSATE](https://osate.org/) plugin. This version of
the tool runs in `java version 1.8`. Download a recent OSATE available for
Windows, Linux and macOS (For Windows, only some features in our tool are
available). You can find plugins of our tools in `OSATE-plugin` directory in
this repository. Make the `dropins` folder in downloaded OSATE directory, and
move out plugins into it. In
`osate/configuration/org.eclipase.equinox.simpleconfigurator`, there is a
`bundles.info` file which contains a list of all the plugins installed in the
current system.

Put the following code under `#version=1` in the `bundles.info` file

> edu.postech.aadl.synch,1.0.0.202104110533,dropins/edu.postech.aadl.synch_1.0.0.202104110533.jar,4,false
> edu.postech.aadl.xtext.propspec,1.0.0.202104110533,dropins/edu.postech.aadl.xtext.propspec_1.0.0.202104110533.jar,4,false
> edu.postech.aadl.xtext.propspec.ui,1.0.0.202104110533,dropins/edu.postech.aadl.xtext.propspec.ui_1.0.0.202104110533.jar,4,false

You can now run `OSATE` and start verification. If you successfully execute
`OSATE` with our plugins, you can find the following window.

![OSATE](https://raw.githubusercontent.com/hybridsynchaadl/HybridSynchAADL/master/images/start.png)

There must be the menu named by "HybridSynchAADL" when our plugins are 
successfullly included in OSATE.

## Getting Started + Example

The easiest way to get started is to run some of the simple HybridSynchAADL
models. Follow the step-by-step instruction guideline in `tutorial.pdf` in
this repository.

Note that HybridSynchAADL tool uses Maude-with-SMT which is a rewriting modulo
SMT extension of Maude 3.0 at C++ level. You can download it in this
[link](https://maude-se.github.io/)

## Installation (for Developer)

For developing HybridSynchAADL, download osate developer version in your local
computer. Please refer to the `Developer Documentation` in OSATE web page. 
After successfully setting up an OSATE development environment, import three plugin projects in this repository:

* edu.postech.aadl.synch
* edu.postech.aadl.xtext.propspec
* edu.postech.aadl.xtext.propspec.ui

HybridSynchAADL uses [ANTLR](https://www.antlr.org/) which is a powerful parser
generator for java when parsing continuous dynamics in environment component.
You can download ANTLR and ANTLR IDE in `Eclilpse Markeplace`.

