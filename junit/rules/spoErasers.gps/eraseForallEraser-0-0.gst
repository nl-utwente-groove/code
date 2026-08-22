<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="eraseForallEraser-0-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n2"/>
        <node id="n1"/>
        <node id="n3"/>
        <node id="n0"/>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>flag:done</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>flag:done</string>
            </attr>
        </edge>
    </graph>
</gxl>
