<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="addToNew">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>255 146 18 21</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>63 210 345 36</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>169 147 18 21</string>
            </attr>
        </node>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>:</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>Two existing A-nodes, both of which should receive an edge</string>
            </attr>
        </edge>
        <edge to="n0" from="n2">
            <attr name="label">
                <string>:</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
    </graph>
</gxl>
