<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="test1">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>164 120 19 19</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>342 135 45 36</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>364 232 19 19</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>219 273 19 19</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
        <edge from="n0" to="n4">
            <attr name="label">
                <string>val</string>
            </attr>
            <attr name="layout">
                <string>500 0 173 129 145 244 228 282 11</string>
            </attr>
        </edge>
        <edge from="n2" to="n0">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge from="n2" to="n4">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n2" to="n3">
            <attr name="label">
                <string>caller</string>
            </attr>
        </edge>
        <edge from="n2" to="n0">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n2" to="n4">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
        <edge from="n4" to="n0">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
    </graph>
</gxl>
