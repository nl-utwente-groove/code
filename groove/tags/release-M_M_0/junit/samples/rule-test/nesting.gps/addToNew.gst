<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="addToNew" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1"/>
        <node id="n2"/>
        <node id="n0"/>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge from="n2" to="n1">
            <attr name="label">
                <string>:</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>Two existing A-nodes, both of which should receive an edge</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge from="n2" to="n0">
            <attr name="label">
                <string>:</string>
            </attr>
        </edge>
    </graph>
</gxl>
