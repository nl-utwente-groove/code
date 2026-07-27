<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="eraseForallEraser-1">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>100 100 34 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>240 40 34 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>240 160 34 46</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
    </graph>
</gxl>
