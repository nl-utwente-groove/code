<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>130 89 60 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>140 245 34 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>314 95 34 46</string>
            </attr>
        </node>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>p</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>p</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>q</string>
            </attr>
            <attr name="layout">
                <string>503 -3 319 128 169 258 11</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:current</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>r</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>p</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>q</string>
            </attr>
            <attr name="layout">
                <string>500 0 157 268 200 186 160 112 12</string>
            </attr>
        </edge>
    </graph>
</gxl>
