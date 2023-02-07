#!/usr/bin/python
# -*- coding: UTF-8 -*-

import requests
import psutil
import time

def send(equipmentId, indicatorId, total, used):
    header={"Content-Type": "application/json;charset=utf8"}
    json={"equipmentId": equipmentId, "indicatorId": indicatorId, "used": used, "total": total, "time": int(time.time() * 1000)}
    res = requests.post(url='http://localhost:8080/realtime?aliasName=usage', json=json, headers=header)
    return res

def send2(metricId, percent):
    header={"Content-Type": "application/json;charset=utf8"}
    json={"metricId": metricId, "value": percent, "time": int(time.time() * 1000)}
    res = requests.post(url='http://localhost:8080/realtime?aliasName=metric', json=json, headers=header)
    return res

def collect():
    try:
        info = psutil.virtual_memory()
        response = send('00', '01', info.total / 1024 / 1024, info.used / 1024 / 1024)
        send2('00_02', psutil.cpu_percent())
        print(response.status_code)
    except OSError as err:
        print("err: {0}".format(err))

def loop_monitor():
    while True:
        collect()
        time.sleep(3)


loop_monitor()

# print(psutil.cpu_count)




# print(info.total / 1024 / 1024)
# print(info.available / 1024 / 1024)