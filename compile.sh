#!/bin/bash

mvn install dependency:copy-dependencies

mvn clean compile
