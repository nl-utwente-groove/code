<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n2"/>
        <node id="n0"/>
        <node id="n5"/>
        <node id="n1"/>
        <node id="n4"/>
        <node id="n3"/>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n5" from="n1">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>e</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
    </graph>
</gxl>