<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="pair">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>145 133 27 36</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>10 8 48 54</string>
            </attr>
        </node>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>flag:hasLeft</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>flag:hasRight</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>hold</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
    </graph>
</gxl>
