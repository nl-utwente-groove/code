<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="append-1-list-1">
        <node id="n114">
            <attr name="layout">
                <string>53 146 18 18</string>
            </attr>
        </node>
        <node id="n115">
            <attr name="layout">
                <string>202 94 18 18</string>
            </attr>
        </node>
        <node id="n112">
            <attr name="layout">
                <string>227 334 18 18</string>
            </attr>
        </node>
        <node id="n111">
            <attr name="layout">
                <string>149 149 18 18</string>
            </attr>
        </node>
        <node id="n113">
            <attr name="layout">
                <string>53 305 18 18</string>
            </attr>
        </node>
        <edge to="n115" from="n115">
            <attr name="label">
                <string>3</string>
            </attr>
        </edge>
        <edge to="n112" from="n113">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n112" from="n112">
            <attr name="label">
                <string>4</string>
            </attr>
        </edge>
        <edge to="n113" from="n113">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge to="n114" from="n114">
            <attr name="label">
                <string>root</string>
            </attr>
        </edge>
        <edge to="n113" from="n113">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge to="n114" from="n113">
            <attr name="label">
                <string>caller</string>
            </attr>
        </edge>
        <edge to="n111" from="n114">
            <attr name="label">
                <string>list</string>
            </attr>
        </edge>
        <edge to="n115" from="n111">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge to="n111" from="n113">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
    </graph>
</gxl>
