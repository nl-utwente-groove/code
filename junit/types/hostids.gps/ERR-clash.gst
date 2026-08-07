<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="ERR-clash">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>100 100 30 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>100 200 30 30</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>id:x</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>id:x</string>
            </attr>
        </edge>
    </graph>
</gxl>
