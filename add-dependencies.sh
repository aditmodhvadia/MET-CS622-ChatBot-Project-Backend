#!/bin/bash

mvn install dependency:copy-dependencies

cp ./target/dependency ./lib
