<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="markTest-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>40 40 60 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>200 40 60 30</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>200 120 60 30</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>200 200 60 30</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>let:x = 2</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>let:y = 1</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>let:y = 3</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>let:y = 5</string>
            </attr>
        </edge>
    </graph>
</gxl>
