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
                <string>213 256 50 22</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>110 144 50 22</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>220 59 50 22</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>218 153 48 22</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>316 145 50 22</string>
            </attr>
        </node>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n4" from="n1">
            <attr name="label">
                <string>e</string>
            </attr>
            <attr name="layout">
                <string>500 0 135 155 186 189 242 164 12</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n1" from="n4">
            <attr name="label">
                <string>l</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n4" from="n2">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>e</string>
            </attr>
            <attr name="layout">
                <string>500 0 245 70 216 117 242 164 12</string>
            </attr>
        </edge>
        <edge to="n0" from="n4">
            <attr name="label">
                <string>f</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
    </graph>
</gxl>
