<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n1"/>
        <node id="n2"/>
        <node id="n0"/>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>mark</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>token</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
    </graph>
</gxl>
