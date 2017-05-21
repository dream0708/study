#!/bin/sh
thrift --gen java -out ../java ./EliteModel.thrift
thrift --gen java -out ../java ./EliteSearchModel.thrift
thrift --gen java -out ../java ./EliteThriftService.thrift

