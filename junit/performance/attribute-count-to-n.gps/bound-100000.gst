<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="bound-100000">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>40 40 30 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>130 40 30 30</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>counter</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:bound=100000</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>int:0</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
    </graph>
</gxl>
