<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>123 155 30 37</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>110 82 60 33</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>192 139 42 37</string>
            </attr>
        </node>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>int:1</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Thing</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>s</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>string:&quot;a&quot;</string>
            </attr>
        </edge>
    </graph>
</gxl>
