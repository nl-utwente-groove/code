<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="merge-0-5">
        <node id="n2">
            <attr name="layout">
                <string>228 301 34 46</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>336 248 34 46</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>399 148 34 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>148 194 34 46</string>
            </attr>
        </node>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
    </graph>
</gxl>
