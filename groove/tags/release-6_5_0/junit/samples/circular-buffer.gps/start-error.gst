<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-error">
        <node id="n112">
            <attr name="layout">
                <string>140 138 40 18</string>
            </attr>
        </node>
        <node id="n111">
            <attr name="layout">
                <string>36 293 43 18</string>
            </attr>
        </node>
        <node id="n108">
            <attr name="layout">
                <string>92 38 30 18</string>
            </attr>
        </node>
        <node id="n109">
            <attr name="layout">
                <string>108 215 30 18</string>
            </attr>
        </node>
        <node id="n110">
            <attr name="layout">
                <string>288 105 30 18</string>
            </attr>
        </node>
        <edge to="n108" from="n109">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n108" from="n108">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n110" from="n112">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge to="n110" from="n110">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n108" from="n112">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge to="n112" from="n112">
            <attr name="label">
                <string>Buffer</string>
            </attr>
        </edge>
        <edge to="n109" from="n110">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n109" from="n109">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n111" from="n111">
            <attr name="label">
                <string>Object</string>
            </attr>
        </edge>
        <edge to="n110" from="n108">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n111" from="n109">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
    </graph>
</gxl>
