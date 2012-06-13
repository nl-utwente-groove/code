<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>138 199 66 32</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>230 129 66 32</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>316 72 66 32</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>134 22 66 32</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>14 92 56 32</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>137 90 66 32</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Station</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>next</string>
            </attr>
            <attr name="layout">
                <string>500 31 171 215 413 38 167 38 14</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Person</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n5" from="n2">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:Person</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Station</string>
            </attr>
        </edge>
        <edge to="n5" from="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:Train</string>
            </attr>
        </edge>
        <edge to="n5" from="n4">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n0" from="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Station</string>
            </attr>
        </edge>
    </graph>
</gxl>
