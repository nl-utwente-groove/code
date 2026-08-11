<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n101"/>
        <node id="n102"/>
        <node id="n100"/>
        <edge from="n100" to="n100">
            <attr name="label">
                <string>use</string>
            </attr>
        </edge>
        <edge from="n102" to="n102">
            <attr name="label">
                <string>new</string>
            </attr>
        </edge>
        <edge from="n100" to="n102">
            <attr name="label">
                <string>new:b</string>
            </attr>
        </edge>
        <edge from="n100" to="n101">
            <attr name="label">
                <string>del:a</string>
            </attr>
        </edge>
        <edge from="n101" to="n101">
            <attr name="label">
                <string>del</string>
            </attr>
        </edge>
    </graph>
</gxl>
