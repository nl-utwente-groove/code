<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-no-cycle">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>191 115 9 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>323 113 8 15</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>327 208 8 15</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>187 30 9 15</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
    </graph>
</gxl>
