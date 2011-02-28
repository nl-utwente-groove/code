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
                <string>322 186 50 22</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>227 116 50 22</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>123 182 48 22</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>188 274 50 22</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>308 265 50 22</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>h</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>t</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
    </graph>
</gxl>
