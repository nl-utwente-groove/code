<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="nestedCount-0-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n6">
            <attr name="layout">
                <string>243 366 41 18</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>427 240 41 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>120 386 32 18</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>213 256 41 18</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>378 315 32 36</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>185 158 32 18</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>355 152 32 18</string>
            </attr>
        </node>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>type:Flower</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>type:Flower</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:Plant</string>
            </attr>
        </edge>
        <edge from="n3" to="n6">
            <attr name="label">
                <string>has</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>type:Flower</string>
            </attr>
        </edge>
        <edge from="n2" to="n5">
            <attr name="label">
                <string>has</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Plant</string>
            </attr>
        </edge>
        <edge from="n2" to="n4">
            <attr name="label">
                <string>has</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>flag:mark</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Plant</string>
            </attr>
        </edge>
        <edge from="n0" to="n4">
            <attr name="label">
                <string>has</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Plant</string>
            </attr>
        </edge>
        <edge from="n1" to="n5">
            <attr name="label">
                <string>has</string>
            </attr>
        </edge>
    </graph>
</gxl>
