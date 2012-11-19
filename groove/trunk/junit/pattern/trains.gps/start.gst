<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n5">
            <attr name="layout">
                <string>152 82 36 48</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>24 84 36 48</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>149 14 36 48</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>316 64 66 48</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>245 121 36 48</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>153 191 36 48</string>
            </attr>
        </node>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:S</string>
            </attr>
        </edge>
        <edge to="n0" from="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:T</string>
            </attr>
        </edge>
        <edge to="n5" from="n4">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:S</string>
            </attr>
        </edge>
        <edge to="n5" from="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:P</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n5" from="n2">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:P</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:S</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>next</string>
            </attr>
            <attr name="layout">
                <string>500 31 184 205 408 37 180 37 14</string>
            </attr>
        </edge>
    </graph>
</gxl>
