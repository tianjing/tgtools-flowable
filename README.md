# tgtools-flowable
tgtools-flowable

## 说明

项目初衷
解决flowable 支持 达梦库。

目前我接触的国产库是 达梦6 7；
由于达梦6 sql 语法支持需要改动很多东西，
参考https://github.com/tianjing/tgtools.activiti.dm.git ;
本项目目前不考虑支持达梦6，优先考虑达梦7的版本。
达梦7与oracle sql差异较少，通过少量的改动即可支持。

版本规则 6.4.2.0 前三位为 flowable 版本号，第四位为本项目版号

## 目标

支持达梦7；

支持模型管理；

正常使用常用的Serice；

取消idm、用户权限表单等，成为一个类似 activiti一样一个独立框架使用；

## 问题

CNNM 案例模型

DMN 决策

这些我都不熟悉，目前不会调整。
