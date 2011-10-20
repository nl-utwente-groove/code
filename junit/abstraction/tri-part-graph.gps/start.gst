<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n2">
            <attr name="layout">
                <string>101 99 48 22</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>31 169 50 22</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>31 99 50 22</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>31 249 50 22</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>31 19 50 22</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>101 169 48 22</string>
            </attr>
        </node>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n5" from="n1">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
    </graph>
</gxl>
