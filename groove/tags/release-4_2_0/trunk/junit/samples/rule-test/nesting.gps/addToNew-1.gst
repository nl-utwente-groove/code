<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="addToNew-1" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1"/>
        <node id="n2"/>
        <node id="n3"/>
        <node id="n0"/>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge from="n3" to="n0">
            <attr name="label">
                <string>:</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>B</string>
            </attr>
        </edge>
        <edge from="n2" to="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge from="n2" to="n0">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>Now they both have their incoming edges</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge from="n3" to="n1">
            <attr name="label">
                <string>:</string>
            </attr>
        </edge>
    </graph>
</gxl>
